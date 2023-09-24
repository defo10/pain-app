module.exports = {
	globDirectory: 'resources/public/',
	globPatterns: [
		'**/*.{png,jpg}'
	],
	swDest: 'resources/public/sw.js',
	ignoreURLParametersMatching: [
		/^utm_/,
		/^fbclid$/
	]
};