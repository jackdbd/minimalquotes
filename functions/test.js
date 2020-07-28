const fs = require('fs');
const path = require('path');
const firebase = require('@firebase/testing');

const TEST_FIREBASE_PROJECT_ID = 'test-firestore-rules-project';

const myAuth = {
  uid: 'jack',
  email: 'jack@example.com',
};

const theirsAuth = {
  uid: 'sam',
  email: 'sam@example.com',
};

const adminAuth = {
  uid: 'bob',
  email: 'bob@example.com',
};

const adminUser = {
  isAdmin: true,
  email: adminAuth.email,
  uid: adminAuth.uid,
};

const dbPublic = firebase
  .initializeTestApp({ projectId: TEST_FIREBASE_PROJECT_ID })
  .firestore();

// Firestore instance for an authenticated, non-admin user
const dbNormal = firebase
  .initializeTestApp({
    projectId: TEST_FIREBASE_PROJECT_ID,
    auth: myAuth,
  })
  .firestore();

// Firestore instance for an authenticated, admin user
const dbAdmin = firebase
  .initializeTestApp({
    projectId: TEST_FIREBASE_PROJECT_ID,
    auth: adminAuth,
  })
  .firestore();

const tag = {
  name: 'wisdom',
  description: 'A tag about wisdom',
  color: 'green',
};

// Before ALL
before(async () => {
  // Load the content of the "firestore.rules" file into the emulator before
  // running the test suite. This is necessary because we are using a fake
  // Project ID in the tests, so the rules "hot reloading" behavior which works
  // in the Web App does not apply here.
  const rulesContent = fs.readFileSync(
    path.resolve(__dirname, '../firestore.rules'),
    'utf8'
  );
  await firebase.loadFirestoreRules({
    projectId: TEST_FIREBASE_PROJECT_ID,
    rules: rulesContent,
  });
});

// After ALL
after(() => {
  // Delete the Firebase test app when the test suite exits.
  firebase.apps().forEach((app) => app.delete());
});

describe('quote documents', () => {
  after(() => {
    firebase.clearFirestoreData({ projectId: TEST_FIREBASE_PROJECT_ID });
  });

  it('can be read by anyone', async () => {
    await firebase.assertSucceeds(dbPublic.doc('quotes/quoteId').get());
  });
});

describe('tag documents', () => {
  after(() => {
    firebase.clearFirestoreData({ projectId: TEST_FIREBASE_PROJECT_ID });
  });

  it('can be read by anyone', async () => {
    await firebase.assertSucceeds(dbPublic.doc('tags/tagId').get());
  });

  it('cannot be created by anyone', async () => {
    await firebase.assertFails(
      dbPublic.doc('tags/tagId').set({
        name: 'wisdom',
        description: 'A tag about wisdom',
        color: 'green',
      })
    );
  });

  it('can be created by an admin user', async () => {
    await dbAdmin.doc(`users/${adminAuth.uid}`).set(adminUser);
    await firebase.assertSucceeds(dbAdmin.doc('tags/tagId').set(tag));
  });

  it('can be deleted by an admin user', async () => {
    await dbAdmin.doc(`users/${adminAuth.uid}`).set(adminUser);
    await dbAdmin.doc('tags/tagId').set(tag);
    await firebase.assertSucceeds(dbAdmin.doc('tags/tagId').delete());
  });

  it('cannot be deleted by a normal user', async () => {
    await dbAdmin.doc(`users/${adminAuth.uid}`).set(adminUser);
    await dbAdmin.doc('tags/tagId').set(tag);
    await firebase.assertFails(dbNormal.doc('tags/tagId').delete());
  });
});

describe('user documents', () => {
  after(() => {
    firebase.clearFirestoreData({ projectId: TEST_FIREBASE_PROJECT_ID });
  });

  it('cannot be read by anyone', async () => {
    await firebase.assertFails(dbNormal.doc('users/userId').get());
  });

  it('can be read by the owner', async () => {
    await firebase.assertSucceeds(dbNormal.doc(`users/${myAuth.uid}`).get());
  });

  it('can be read by an admin', async () => {
    await dbAdmin.doc(`users/${adminAuth.uid}`).set(adminUser);
    await firebase.assertSucceeds(dbAdmin.doc('users/userId').get());
  });

  it('cannot be read by any other non-admin user', async () => {
    await firebase.assertFails(dbNormal.doc(`users/${theirsAuth.uid}`).get());
  });
});
