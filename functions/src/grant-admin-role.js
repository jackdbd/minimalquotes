const functions = require('firebase-functions');

const grantAdmin = async (admin, email) => {
  let user;
  try {
    user = await admin.auth().getUserByEmail(email);
  } catch (error) {
    throw error;
  }

  // exit early if the user is already an admin
  if (
    user.customClaims &&
    user.customClaims.roles &&
    user.customClaims.roles.admin
  ) {
    return;
  } else {
    return admin.auth().setCustomUserClaims(user.uid, {
      roles: {
        admin: true,
      },
    });
  }
};

/**
 * Factory function that returns a Firebase Callable function that grants
 * the `admin` role to the specified user.
 *
 * See https://firebase.google.com/docs/auth/admin/create-custom-tokens
 * for more information on creating custom tokens.
 */
const makeGrantAdminRole = (admin) => {
  return functions.https.onCall(async (data, context) => {
    if (!context.auth.token.roles || context.auth.token.roles.admin !== true) {
      // https://firebase.google.com/docs/reference/functions/providers_https_#functionserrorcode
      throw new functions.https.HttpsError(
        'permission-denied',
        'Must be an admin user to grant an admin role.'
      );
    }
    if (!data.email) {
      throw new functions.https.HttpsError(
        'invalid-argument',
        'Must provide an email.'
      );
    }

    try {
      await grantAdmin(admin, data.email);
      return { result: `Request fulfilled. ${data.email} is now an admin.` };
    } catch (error) {
      throw new functions.https.HttpsError('not-found', error.message);
    }
  });
};

module.exports = {
  grantAdmin,
  makeGrantAdminRole,
};
