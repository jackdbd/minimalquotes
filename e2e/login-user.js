const { click, closeBrowser, goto, openBrowser, textBox, write } = require('taiko');

const debug = true;

const browserOptions = debug ? {
    headless: false,
    observe: true
} : {}

console.log("TEST", browserOptions);

// TODO: this test would be ok for an authenticated user. But how to login with taiko?

(async () => {
    try {
        await openBrowser(browserOptions);
        await goto("localhost:3000");

        await click("Sign in");
        await click("Sign in with Google");
        await write("test-email-here");
        await click("Next");
        await write("test-password-here");

        // The test fails because Google says the browser is insecure (automated chromium)
    } catch (error) {
        console.error(error);
    } finally {
        await closeBrowser();
    }
})();
