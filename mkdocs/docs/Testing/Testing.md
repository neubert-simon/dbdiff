# Testing

## Naming convention

### Method Names

Tests are named in the following manner:

`testMethodName_condition_expectedResult`

- testMethodName

The name of the method that is being tested, prefixed with the word `test`.

- condition

The condition that is being tested, e.g. `withNullArguments`.

- expectedResult

The outcome that should result from calling the method under the previously described conditions, e.g. `throwsCustomException`.

### Class Names
Test classes are named after the classes they test, with a suffix appended dependant on the type of test:

- Test
`Test` is appendeded to the name of the class in case of *unit testing*.

- TestIT
`TestIT` is appendeded to the name of the class in case of *integration testing*.

- E2E
`E2E` is appendeded to the name of the class in case of *end-to-end testing*.