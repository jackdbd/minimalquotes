module.exports = {
  plugins: [require('@tailwindcss/custom-forms')],
  // purge works only when NODE_ENV=production
  // https://github.com/tailwindlabs/tailwindcss/pull/1639
  purge: [
    './src/**/*.cljs',
    './public/dev.html',
    './public/index.html',
    './public/prod.html',
  ],
  theme: {
    extend: {
      colors: {
        'brand-blue': '#1992d4',
      },
    },
  },
  variants: {
    opacity: ['responsive', 'hover'],
  },
};
