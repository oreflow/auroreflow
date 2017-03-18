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
                loader: 'css-loader'
            }
        ]
    },
    entry: './app/boot.ts',
    output: {
        path: '/',
        filename: 'auroreflow.bundle.js'
    }
};
