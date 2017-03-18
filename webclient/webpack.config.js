const path = require('path');
var HtmlWebpackPlugin = require('html-webpack-plugin');

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
            },
            {
                test: /\.jpg$/,
                loader: ['file-loader']
            }

        ]
    },
    entry: './app/boot.ts',
    plugins: [
        new HtmlWebpackPlugin({
            title: 'Auroreflow',
            template: 'index.html',
            chunksSortMode: 'dependency'
        })
    ],
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'auroreflow.bundle.js'
    }
};
