# OhMyCards - Web!

Please read the [Readme of OhMyCards](https://github.com/vitorqb/oh-my-cards).

## Development

- Make sure you node version agrees with [](./nvmrc)

```sh
# Installs all dependencies
make install

# Start all builds for development and watch for changes
make watch

# Start compiling the scss
make scss

# Starts a backend
make runBackend
make rev-proxy
```

### Tests

There are two ways of checking the tests. Both require `make watch` to be running.
1. Open http://127.0.0.1:9050
2. Run `make karma`
