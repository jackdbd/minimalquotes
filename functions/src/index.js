const { makeGrantAdminRole } = require('./grant-admin-role');
const { makeListAllUsers } = require('./list-all-users');

module.exports = {
  makeGenerateFakeQuotes: require('./generate-fake-quotes'),
  makeGenerateFakeTags: require('./generate-fake-tags'),
  makeGrantAdminRole,
  makeListAllUsers,
  recursiveDelete: require('./recursive-delete'),
  scheduledFirestoreExport: require('./scheduled-firestore-export'),
};
