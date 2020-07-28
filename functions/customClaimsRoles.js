const admin = require('firebase-admin');

const projectId = 'minimalquotes-5c472';
console.log(`Running on ${projectId}`);
// you can get the Admin SDK service account key from the url bellow, remember
// to add your project Id
// https://console.firebase.google.com/u/0/project/{{PROJECT_ID}}/settings/serviceaccounts/adminsdk
var serviceAccount = require(`./firebase-service-account.json`);
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: `https://${projectId}.firebaseio.com`,
});

const auth = admin.auth();

// sets the custom claims on an account to the claims object provided
const setClaims = async (email, claims) => {
  const user = await auth.getUserByEmail(email);
  console.log(user);
  auth.setCustomUserClaims(user.uid, claims);
};

setClaims('jackdebidda@gmail.com', {
  roles: ['ADMIN'],
});

exports.auth = auth;
