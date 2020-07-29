const functions = require('firebase-functions');

/**
 * Recursively iterate over Firebase Auth users in batches.
 * See https://firebase.google.com/docs/reference/admin/node/admin.auth.Auth#listusers
 * @param {*} admin
 * @param {number} maxResults
 * @param {object} userProps
 * @param {string | undefined} pageToken
 * @param {array} users
 * @param {boolean} calledAtLeastOnce
 */
const recursiveListUsers = async (
  admin,
  maxResults,
  userProps,
  pageToken = undefined,
  users = [],
  calledAtLeastOnce = false
) => {
  if (users.length > 0 && pageToken === undefined) {
    return users;
  }
  // If we do not provide a pageToken and there are no users, I think we need
  // this check to avoid an infinite recursion.
  if (calledAtLeastOnce && pageToken === undefined) {
    return users;
  }

  try {
    const listUsersResult = await admin.auth().listUsers(maxResults, pageToken);
    listUsersResult.users.forEach((userRecord) => {
      // users.push(userRecord.toJSON());
      let user = {};
      userProps.forEach((prop) => {
        user[prop] = userRecord[prop];
      });
      users.push(user);
    });
    return await recursiveListUsers(
      admin,
      maxResults,
      userProps,
      listUsersResult.pageToken,
      users,
      true
    );
  } catch (error) {
    return error;
  }
};

const makeListAllUsers = (admin) => {
  return functions.https.onCall(async (data, context) => {
    if (!context.auth.token.roles || context.auth.token.roles.admin !== true) {
      throw new functions.https.HttpsError(
        'permission-denied',
        'Must be an admin user to grant an admin role.'
      );
    }

    let maxResults = 1000;
    if (data.batchSize) {
      maxResults = data.batchSize;
    }

    let userProps = [
      'customClaims',
      'displayName',
      'email',
      'metadata',
      'photoURL',
      'uid',
    ];
    if (data.userProps) {
      userProps = data.userProps;
    }

    try {
      const users = await recursiveListUsers(admin, maxResults, userProps);
      return { result: `Request fulfilled.`, users };
    } catch (error) {
      return { error };
    }
  });
};

module.exports = {
  makeListAllUsers,
  recursiveListUsers,
};
