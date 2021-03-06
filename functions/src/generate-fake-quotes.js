const faker = require('faker');
const functions = require('firebase-functions');

const makeFakeQuote = (tagIds) => {
  const author = faker.name.findName();
  const nSentences = faker.random.number({ min: 1, max: 3 });
  const text = faker.lorem.sentences(nSentences);
  const nTags = faker.random.number({ min: 0, max: 3 });

  let tagArray;
  if (tagIds.length > 0) {
    tagArray = [...Array(nTags)].map(() => {
      const i = faker.random.number({ min: 0, max: tagIds.length - 1 });
      return tagIds[i];
    });
  } else {
    tagArray = [...Array(nTags)].map(() => faker.random.uuid());
  }

  const reducer = (acc, cv) => {
    acc[cv] = true;
    return acc;
  };
  const tagMap = tagArray.reduce(reducer, {});

  // console.log('tagMap', tagMap);
  // console.log('author', author, 'text', text, 'tags', tags);
  return { author, tags: tagMap, text };
};

/**
 * Factory function that returns a Firebase Callable function that generates
 * fake quotes.
 *
 * See https://firebase.google.com/docs/auth/admin/create-custom-tokens
 * for more information on creating custom tokens.
 */
const makeGenerateFakeQuotes = (admin) => {
  return functions.https.onCall(async (data, context) => {
    if (!context.auth.token.roles || context.auth.token.roles.admin !== true) {
      // https://firebase.google.com/docs/reference/functions/providers_https_#functionserrorcode
      throw new functions.https.HttpsError(
        'permission-denied',
        'Must be an admin user to generate mock data.'
      );
    }

    let n;
    if (data.n) {
      n = data.n;
    } else {
      n = 5;
    }

    let tagIds = [];
    if (data.tagIds) {
      tagIds = data.tagIds;
    }

    // if provided by the caller, use a seed for consistent results.
    if (data.seed) {
      faker.seed(data.seed);
    }

    const batch = admin.firestore().batch();
    [...Array(n)].forEach(() => {
      const docRef = admin.firestore().collection('quotes').doc();
      batch.set(docRef, {
        ...makeFakeQuote(tagIds),
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        createdBy: context.auth.token.uid,
        lastEditedAt: admin.firestore.FieldValue.serverTimestamp(),
        lastEditedBy: context.auth.token.uid,
      });
    });

    try {
      const results = await batch.commit();
      return { result: `generated ${results.length} fakes` };
    } catch (error) {
      return { error };
    }
  });
};

module.exports = makeGenerateFakeQuotes;
