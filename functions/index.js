const faker = require('faker');
const functions = require('firebase-functions');

const admin = require('firebase-admin');

// The Firebase Admin SDK can also be initialized with no parameters.
// In this case, the SDK uses Google Application Default Credentials and reads
// options from the FIREBASE_CONFIG environment variable.

admin.initializeApp();

//set seed for consistent results
// faker.seed(123);

const makeFakeUser = () => {
  const displayName = faker.name.findName();
  const email = faker.internet.email();
  const photoUrl = faker.image.avatar();
  const uid = faker.random.uuid();
  return { displayName, email, photoUrl, uid };
};

// const randomCard = faker.helpers.createCard();

const makeFakeQuote = () => {
  const author = faker.name.findName();
  const nSentences = faker.random.number({ min: 1, max: 3 });
  const text = faker.lorem.sentences(nSentences);
  const nTags = faker.random.number({ min: 0, max: 5 });
  const tags = [...Array(nTags)].map(() => faker.random.uuid());
  return { author, tags, text };
};

const generateFakeQuotes = functions.firestore
  .document('tags/love')
  .onUpdate(async (change, context) => {
    const batch = admin.firestore().batch();

    [...Array(3)].forEach(() => {
      const newRef = admin.firestore().collection('quotes').doc();
      batch.set(newRef, {
        ...makeFakeQuote(),
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        createdBy: 'cloud-function-generateFakeQuotes',
        lastEditedAt: admin.firestore.FieldValue.serverTimestamp(),
        lastEditedBy: 'cloud-function-generateFakeQuotes',
      });
    });

    try {
      const results = await batch.commit();
      console.log(`=== Generated ${results.length} fakes ===`);
    } catch (err) {
      console.error('=== COULD NOT WRITE FAKES ===');
      console.error(err);
    }

    return null;
  });

const logWhenLoveTagChanges = functions.firestore
  .document('tags/love')
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();
    const timestamp = context.timestamp;

    console.log(`=== Tag love was updated on ${timestamp} ===`);
    console.log('from', before, 'to', after);
    return null;
  });

const addFakeQuoteEveryThreeMinutes = functions.pubsub
  .schedule('every 3 minutes')
  .onRun(async (context) => {
    const docRef = await admin
      .firestore()
      .collection('quotes')
      .add({
        ...makeFakeQuote(),
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        createdBy: 'cloud-function-addFakeQuoteEveryThreeMinutes',
        lastEditedAt: admin.firestore.FieldValue.serverTimestamp(),
        lastEditedBy: 'cloud-function-addFakeQuoteEveryThreeMinutes',
      });

    console.log(`Add document ${docRef.id} at ${docRef.path}`);
    return docRef.id;
  });

// https://github.com/firebase/snippets-node/blob/ee92bd108514bb2eac6651145d789d31d4bf9f07/firestore/solution-deletes/functions/index.js#L28-L72
/**
 * Initiate a recursive delete of documents at a given path.
 *
 * The calling user must be authenticated and have the custom "admin" attribute
 * set to true on the auth token.
 *
 * This delete is NOT an atomic operation and it's possible
 * that it may fail after only deleting some documents.
 *
 * @param {string} data.path the document or collection path to delete.
 */
const recursiveDelete = functions
  .runWith({
    timeoutSeconds: 540,
    memory: '2GB',
  })
  .https.onCall(async (data, context) => {
    const firebase_tools = require('firebase-tools');
    // Only allow admin users to execute this function.
    if (!(context.auth && context.auth.token && context.auth.token.admin)) {
      throw new functions.https.HttpsError(
        'permission-denied',
        'Must be an administrative user to initiate delete.'
      );
    }
    const path = data.path;
    console.log(
      `=== User ${context.auth.uid} has requested to delete path ${path} ===`
    );

    console.log('functions.config().fb', functions.config().fb);

    // Run a recursive delete on the given document or collection path.
    // The 'token' must be set in the functions config, and can be generated
    // at the command line by running 'firebase login:ci'.
    await firebase_tools.firestore.delete(path, {
      project: process.env.GCLOUD_PROJECT,
      recursive: true,
      yes: true,
      // token: functions.config().fb.token,
    });

    return {
      path: path,
    };
  });

/**
 * Callable function that creates a custom auth token with the
 * custom attribute "admin" set to true.
 *
 * See https://firebase.google.com/docs/auth/admin/create-custom-tokens
 * for more information on creating custom tokens.
 *
 * @param {string} data.uid the user UID to set on the token.
 */
const mintAdminToken = functions.https.onCall(async (data, context) => {
  const uid = data.uid;

  const token = await admin.auth().createCustomToken(uid, { admin: true });

  return { token };
});

// exports.addFakeQuoteEveryThreeMinutes = addFakeQuoteEveryThreeMinutes;
exports.generateFakeQuotes = generateFakeQuotes;
exports.logWhenLoveTagChanges = logWhenLoveTagChanges;
exports.mintAdminToken = mintAdminToken;
exports.recursiveDelete = recursiveDelete;
