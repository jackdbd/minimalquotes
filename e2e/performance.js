const fs = require('fs');
const { closeBrowser, diagnostics, goto, openBrowser } = require('taiko');

const {
  endTracing,
  getPerformanceMetrics,
  getTracingLogs,
  startTracing,
} = diagnostics;

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

    await startTracing();
    await goto('localhost:3000');
    await endTracing();

    const {
      domContentLoaded,
      firstMeaningfulPaint,
      timeToFirstInteractive,
    } = await getPerformanceMetrics();
    console.log(`domContentLoaded: ${domContentLoaded}ms`);
    console.log(`firstMeaningfulPaint: ${firstMeaningfulPaint}ms`);
    console.log(`timeToFirstInteractive: ${timeToFirstInteractive}ms`);

    fs.writeFileSync(
      'out/tracelog.json',
      JSON.stringify(await getTracingLogs())
    );
    console.log(
      `Tracelog saved. Import it in Chrome DevTools -> Performance Tab -> Load profile`
    );
  } catch (error) {
    console.error(error);
  } finally {
    await closeBrowser();
  }
};

systemUnderTest();
