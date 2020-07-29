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
  roles: {
    admin: true,
  },
};

const adminUser = {
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

const quote = {
  author: 'Buddha',
  tags: {
    tagId: true,
  },
  text:
    'The past is already gone, the future is not yet here. There’s only one moment for you to live.',
};

const tag = {
  color: 'green',
  description: 'A tag about wisdom',
  name: 'wisdom',
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

  it('can be created by an admin user', async () => {
    await firebase.assertSucceeds(
      dbAdmin.doc('quotes/quoteId').set({
        ...quote,
        createdAt: firebase.firestore.Timestamp.now(),
        createdBy: adminUser.uid,
      })
    );
  });

  it('cannot be created by anyone', async () => {
    await firebase.assertFails(
      dbPublic.doc('quotes/quoteId').set({
        ...quote,
        createdAt: firebase.firestore.Timestamp.now(),
        createdBy: adminUser.uid,
      })
    );
  });

  it('can be deleted by an admin user', async () => {
    await dbAdmin.doc('quotes/quoted').set(tag);
    await firebase.assertSucceeds(dbAdmin.doc('quotes/tagId').delete());
  });

  it('can be updated by the creator', async () => {
    dbAdmin.doc('quotes/quoteId').set({
      ...quote,
      createdAt: firebase.firestore.Timestamp.now(),
      createdBy: adminUser.uid,
    });
    await firebase.assertSucceeds(
      dbAdmin.doc('quotes/quoteId').update({
        lastUpdatedAt: firebase.firestore.Timestamp.now(),
        lastUpdatedBy: adminUser.uid,
      })
    );
  });
});

describe('tag documents', () => {
  after(() => {
    firebase.clearFirestoreData({ projectId: TEST_FIREBASE_PROJECT_ID });
  });

  it('can be read by anyone', async () => {
    await firebase.assertSucceeds(dbPublic.doc('tags/tagId').get());
  });

  it('can be created by an admin user', async () => {
    await firebase.assertSucceeds(
      dbAdmin.doc('tags/tagId').set({
        ...tag,
        createdAt: firebase.firestore.Timestamp.now(),
      })
    );
  });

  it('cannot be created by anyone', async () => {
    await firebase.assertFails(
      dbPublic.doc('tags/tagId').set({
        ...tag,
        createdAt: firebase.firestore.Timestamp.now(),
        createdBy: adminUser.uid,
      })
    );
  });

  it('can be deleted by an admin user', async () => {
    await dbAdmin.doc('tags/tagId').set(tag);
    await firebase.assertSucceeds(dbAdmin.doc('tags/tagId').delete());
  });

  it('cannot be deleted by a normal user', async () => {
    await dbAdmin.doc('tags/tagId').set(tag);
    await firebase.assertFails(dbNormal.doc('tags/tagId').delete());
  });
});

// describe('user documents', () => {
//   after(() => {
//     firebase.clearFirestoreData({ projectId: TEST_FIREBASE_PROJECT_ID });
//   });

//   it('cannot be read by anyone', async () => {
//     await firebase.assertFails(dbNormal.doc('users/userId').get());
//   });

//   it('can be read by the owner', async () => {
//     await firebase.assertSucceeds(dbNormal.doc(`users/${myAuth.uid}`).get());
//   });

//   it('can be read by an admin', async () => {
//     await dbAdmin.doc(`users/${adminAuth.uid}`).set(adminUser);
//     await firebase.assertSucceeds(dbAdmin.doc('users/userId').get());
//   });

//   it('cannot be read by any other non-admin user', async () => {
//     await firebase.assertFails(dbNormal.doc(`users/${theirsAuth.uid}`).get());
//   });
// });
