const functions = require('firebase-functions');

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
module.exports = functions
  .runWith({
    timeoutSeconds: 540,
    memory: '2GB',
  })
  .https.onCall(async (data, context) => {
    const firebase_tools = require('firebase-tools');
    if (!context.auth.roles || context.auth.token.admin !== true) {
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

    // TODO: where does firebase_tools expect `admin` in the token?

    // Run a recursive delete on the given document or collection path.
    // The 'token' must be set in the functions config, and can be generated
    // at the command line by running 'firebase login:ci'.
    try {
      await firebase_tools.firestore.delete(path, {
        project: process.env.GCLOUD_PROJECT,
        recursive: true,
        // token: context.auth.token,
        yes: true,
        // token: functions.config().fb.token,
      });
      return { result: `documents at path ${path} deleted` };
    } catch (error) {
      return { error };
    }
  });
