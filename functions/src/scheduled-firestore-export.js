const functions = require('firebase-functions');

// TODO: I'm getting 'The caller does not have permission'. I guess I need to
// configure the permissions on the storage bucket.
module.exports = functions.pubsub
  .schedule('every 24 hours')
  .onRun(async (context) => {
    const firestore = require('@google-cloud/firestore');
    const client = new firestore.v1.FirestoreAdminClient();
    const projectId = process.env.GCP_PROJECT || process.env.GCLOUD_PROJECT;
    const databaseName = client.databasePath(projectId, '(default)');

    // TODO: avoid hardcoding the storage bucket's name
    const bucket = 'gs://minimalquotes-5c472.appspot.com';

    // console.log('projectId', projectId);
    // console.log('databaseName', databaseName);

    try {
      const responses = await client.exportDocuments({
        name: databaseName,
        outputUriPrefix: bucket,
        // Leave collectionIds empty to export all collections
        // or set to a list of collection IDs to export,
        // collectionIds: ['users', 'posts']
        collectionIds: [],
      });
      const response = responses[0];
      console.log(`Operation Name: ${response['name']}`);
    } catch (err) {
      console.error(err);
      throw new Error('Export operation failed');
    }
  });
