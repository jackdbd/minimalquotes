const admin = require('firebase-admin');

const {
  makeGenerateFakeQuotes,
  makeGrantAdminRole,
  makeListAllUsers,
  makeMintCustomToken,
  recursiveDelete,
  scheduledFirestoreExport,
} = require('./src');

// The Firebase Admin SDK can be initialized with or without parameters.
// Without parameters the SDK uses Google Application Default Credentials and
// reads options from the FIREBASE_CONFIG environment variable.
// https://firebase.google.com/docs/reference/admin/node/admin
// https://firebase.google.com/docs/admin/setup
admin.initializeApp();

// TODO: pass both admin and environment variables in a context?
const context = { admin };

module.exports = {
  generateFakes: makeGenerateFakeQuotes(admin),
  grantAdminRole: makeGrantAdminRole(admin),
  listAllUsers: makeListAllUsers(admin),
  mintCustomToken: makeMintCustomToken(admin),
  recursiveDelete,
  scheduledFirestoreExport,
};
