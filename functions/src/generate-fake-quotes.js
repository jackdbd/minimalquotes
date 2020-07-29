const faker = require('faker');
const functions = require('firebase-functions');

const makeFakeQuote = () => {
  const author = faker.name.findName();
  const nSentences = faker.random.number({ min: 1, max: 3 });
  const text = faker.lorem.sentences(nSentences);
  const nTags = faker.random.number({ min: 0, max: 5 });
  const tags = [...Array(nTags)].map(() => faker.random.uuid());
  return { author, tags, text };
};

/**
 * Factory function that returns a Firebase Callable function that generates
 * fake quotes.
 *
 * See https://firebase.google.com/docs/auth/admin/create-custom-tokens
 * for more information on creating custom tokens.
 */
const makeGenerateFakeQuotes = (admin) => {
  return functions.https.onCall(async (data, context) => {
    if (!context.auth.token.roles || context.auth.token.roles.admin !== true) {
      // https://firebase.google.com/docs/reference/functions/providers_https_#functionserrorcode
      throw new functions.https.HttpsError(
        'permission-denied',
        'Must be an admin user to generate mock data.'
      );
    }

    let n;
    if (data.n) {
      n = data.n;
    } else {
      n = 5;
    }

    // if provided by the caller, use a seed for consistent results.
    if (data.seed) {
      faker.seed(data.seed);
    }

    const batch = admin.firestore().batch();
    [...Array(n)].forEach(() => {
      const docRef = admin.firestore().collection('quotes').doc();
      batch.set(docRef, {
        ...makeFakeQuote(),
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        createdBy: context.auth.token.uid,
        lastEditedAt: admin.firestore.FieldValue.serverTimestamp(),
        lastEditedBy: context.auth.token.uid,
      });
    });

    try {
      const results = await batch.commit();
      return { result: `generated ${results.length} fakes` };
    } catch (error) {
      return { error };
    }
  });
};

module.exports = makeGenerateFakeQuotes;
