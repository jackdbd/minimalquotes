// const functions = require('firebase-functions');

// const sendWelcomeEmail = functions.auth.user().onCreate(async (user) => {
//   console.log(
//     '=== USER CREATED ===',
//     user.displayName,
//     user.uid,
//     user.email,
//     user.customClaims
//   );
//   console.log('--- assign custom claims here ---');
//   await setClaims('jackdebidda@gmail.com', {
//     roles: {
//       admin: false,
//     },
//   });
// });

// const logWhenLoveTagChanges = functions.firestore
//   .document('tags/love')
//   .onUpdate(async (change, context) => {
//     const before = change.before.data();
//     const after = change.after.data();
//     const timestamp = context.timestamp;

//     console.log(`=== Tag love was updated on ${timestamp} ===`);
//     console.log('from', before, 'to', after);
//     return null;
//   });

// const makeFakeUser = () => {
//   const displayName = faker.name.findName();
//   const email = faker.internet.email();
//   const photoUrl = faker.image.avatar();
//   const uid = faker.random.uuid();
//   return { displayName, email, photoUrl, uid };
// };

// const randomCard = faker.helpers.createCard();

// const addFakeQuoteEveryThreeMinutes = functions.pubsub
//   .schedule('every 3 minutes')
//   .onRun(async (context) => {
//     const docRef = await admin
//       .firestore()
//       .collection('quotes')
//       .add({
//         ...makeFakeQuote(),
//         createdAt: admin.firestore.FieldValue.serverTimestamp(),
//         createdBy: 'cloud-function-addFakeQuoteEveryThreeMinutes',
//         lastEditedAt: admin.firestore.FieldValue.serverTimestamp(),
//         lastEditedBy: 'cloud-function-addFakeQuoteEveryThreeMinutes',
//       });

//     console.log(`Add document ${docRef.id} at ${docRef.path}`);
//     return docRef.id;
//   });

const { makeGrantAdminRole } = require('./grant-admin-role');
const { makeListAllUsers } = require('./list-all-users');

module.exports = {
  // logWhenLoveTagChanges,
  makeGenerateFakeQuotes: require('./generate-fake-quotes'),
  makeGrantAdminRole,
  makeListAllUsers,
  recursiveDelete: require('./recursive-delete'),
  scheduledFirestoreExport: require('./scheduled-firestore-export'),
  // sendWelcomeEmail,
};
