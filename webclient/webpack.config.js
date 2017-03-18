const path = require('path');


module.exports = {
    resolve: {
        extensions: ['.js', '.ts', '.tsx', '.html', '.css', '.scss']
    },
    module: {
        loaders: [
            {
                test: /\.html$/,
                loader: 'html-loader'
            },
            {
                test: /\.ts$/,
                loader: 'ts-loader',
                exclude: '/node_modules'
            },
            {
                test: /\.css$/,
                loader: ['style-loader', 'css-loader']
            },
            {
                test: /\.scss/,
                loader: ['to-string-loader', 'style-loader', 'css-loader', 'sass-loader']
            }
        ]
    },
    entry: './app/boot.ts',
    output: {
        path: '/',
        filename: 'auroreflow.bundle.js'
    }
};
