const functions = require('firebase-functions');

/**
 * Callable function that creates a custom auth token with the
 * custom attribute "admin" set to true.
 *
 * See https://firebase.google.com/docs/auth/admin/create-custom-tokens
 * for more information on creating custom tokens.
 *
 * @param {string} data.uid the user UID to set on the token.
 */
const makeMintCustomToken = (admin) => {
  return functions.https.onCall(async (data, context) => {
    if (!context.auth.token.roles || context.auth.token.roles.admin !== true) {
      // https://firebase.google.com/docs/reference/functions/providers_https_#functionserrorcode
      throw new functions.https.HttpsError(
        'permission-denied',
        'Must be an admin user to mint a token.'
      );
    }

    const uid = data.uid;
    const additionalClaims = { admin: true };

    try {
      const token = await admin.auth().createCustomToken(uid, additionalClaims);
      return { result: `Request fulfilled. Custom token minted.`, token };
    } catch (error) {
      return { error };
    }
  });
};

module.exports = makeMintCustomToken;
