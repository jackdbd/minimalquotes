const {
  openBrowser,
  goto,
  click,
  hover,
  screenshot,
  closeBrowser,
  screencast,
} = require('taiko')

const debug = true

const browserOptions = debug
  ? {
      headless: true,
      observe: false,
    }
  : {}

// console.log('TEST', browserOptions)

const systemUnderTest = async () => {
  try {
    await openBrowser(browserOptions)
    await screencast.startScreencast(
      'out/go-to-tags-page-and-hover-wisdom-tag.gif'
    )
    await goto('localhost:3000')

    await click('Tags')
    await hover('wisdom')
    await screenshot({ path: 'out/hover-on-wisdom-tag.png' })
  } catch (error) {
    console.error(error)
  } finally {
    await screencast.stopScreencast()
    await closeBrowser()
  }
}

systemUnderTest()
