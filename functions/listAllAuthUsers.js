const admin = require('firebase-admin');
const { recursiveListUsers } = require('./src/list-all-users');

// you can get the Admin SDK service account key from the url bellow, remember
// to add your project Id
// https://console.firebase.google.com/u/0/project/{{PROJECT_ID}}/settings/serviceaccounts/adminsdk
const serviceAccount = require(`./firebase-service-account.json`);

const projectId = 'minimalquotes-5c472';
console.log(`Running on ${projectId}`);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: `https://${projectId}.firebaseio.com`,
});

const batchSize = 10;
recursiveListUsers(admin, batchSize).then(console.log).catch(console.error);
