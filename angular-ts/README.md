# DRES Example Client: Angular / TypeScript

Back to the [DRES Example Clients](../README.md)

In this project, there is example code on how to use the DRES Client library for angular/ typescript.

The main example code is in the [app.component.ts](src/app/app.component.ts) file.

## Setup

Please run
```
./gradlew openApiGenerate
```

to get the client side bindings.

Then run

```
npm install
```

Obviously, this expects an NPM installation.

Subsequently, use the [Dev Server](#Developement%20Server) instructions
to get a dev server up and running.

## Existing Angular Project

_This section is for angular developers that want to use the client example lessons learned in their own project._

If you already have an existing Angular project, it might be much easier to simply add the openapi generation to your `package.json`:

```json
{
  "scripts": {
    "gen-dres-client": "openapi-generator-cli generate -g typescript-angular -i https://raw.githubusercontent.com/dres-dev/DRES/master/doc/oas-client.json -o openapi/dres --skip-validate-spec --additional-properties npmName=@dres-client-openapi/api,ngVersion=13.0.0,enumPropertyNaming=original"
  }
}
```

Obviously, there are a lot of parameters you might want to adjust, the above statement is an example to add a script,
to let the openapi generator via npm generate appropriate client bindings for the OpenApi DRES Specs on master branch (probably the latest release).

The (dev-)dependency `"openapi-generator": "^0.1.39",` is required.

In order to generate the bindings, run
```
npm run gen-dres-client
```

---
# Instructions for Angular:

## Angular

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 11.2.6.

### Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

### Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

### Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

### Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

### Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

### Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.
