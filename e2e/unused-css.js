const fs = require('fs');
const { closeBrowser, diagnostics, goto, openBrowser } = require('taiko');

const { prettyCSS, startCssTracing, stopCssTracing } = diagnostics;

const debug = true;
const browserOptions = debug
  ? {
      headless: true,
      observe: false,
    }
  : {};

const systemUnderTest = async () => {
  try {
    await openBrowser(browserOptions);

    await startCssTracing();
    await goto('localhost:3000');
    const cssCoverage = await stopCssTracing();
    await prettyCSS(cssCoverage);
  } catch (error) {
    console.error(error);
  } finally {
    await closeBrowser();
  }
};

systemUnderTest();
