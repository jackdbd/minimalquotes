const admin = require('firebase-admin');
const { grantAdmin } = require('./src/grant-admin-role');

const projectId = 'minimalquotes-5c472';
console.log(`Running on ${projectId}`);
// you can get the Admin SDK service account key from the url bellow, remember
// to add your project Id
// https://console.firebase.google.com/u/0/project/{{PROJECT_ID}}/settings/serviceaccounts/adminsdk
const serviceAccount = require(`./firebase-service-account.json`);
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: `https://${projectId}.firebaseio.com`,
});

const email = 'jackdebidda@gmail.com';
grantAdmin(admin, email).then(console.log).catch(console.error);
