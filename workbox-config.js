module.exports = {
	globDirectory: 'docs/',
	globPatterns: [
		'**/*.{png,jpg}'
	],
	swDest: 'docs/sw.js',
	ignoreURLParametersMatching: [
		/^utm_/,
		/^fbclid$/
	]
};