const { openBrowser, goto, click, hover, screenshot, closeBrowser } = require('taiko');

const debug = true;

const browserOptions = debug ? {
    headless: false,
    observe: true
} : {}

console.log("TEST", browserOptions);

(async () => {
    try {
        await openBrowser(browserOptions);
        await goto("localhost:3000");

        await click("Tags");
        await hover("wisdom");
        await screenshot({path: "screenshots/hover-on-wisdom-tag.png"});
    } catch (error) {
        console.error(error);
    } finally {
        await closeBrowser();
    }
})();
