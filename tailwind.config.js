const windmill = require('@windmill/react-ui/config');

module.exports = windmill({
  plugins: [],
  purge: [],
  theme: {
    extend: {},
  },
  variants: {
    opacity: ['responsive', 'hover'],
  },
});
