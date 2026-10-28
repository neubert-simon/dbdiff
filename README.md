# dbdiff

A command line tool to create diffs between database snapshots.

## Feature Roadmap

### v1.0 Minimum Viable Product

- Connect to SQL based database engines using a configuration file.
    - Dynamically load the driver.
    - Provide the users with detailed and actionable error messages on connection failure.
- Create (named) snapshots of table contents.
- Create snapshots (dumps) of tables in JSON format
- Diff two snapshots.
    - Show a paginated view of all records that differ between snapshots.
- Export diffs into a file 

### v1.1 Quality of Life Features

- Filter to only show added, deleted, or modified records.
- Select or ignore columns when creating diffs (filter actual diff detection).
- Select or ignore columns when displaying diffs (filter output only).
- Save and export diff results (e.g. to JSON).
- Snapshot and saved file / config file management (list, name, delete ...)

<hr>

### v1.x Stretch goals

- Provide an elegant way to diff data if the schema has changed between snapshots
- Specify queries to create snapshots
- Execution of SQL queries/scripts, creating a diff for the result.
    - Provide previews of diffs before committing the transaction.
- GUI based diff display (e.g. static html generation from diff)

<br><br>

## User Experience Design for v1.0

### Basic Usage

`-h, --help`
Show this help page.

`-c` *config*, `--database` *config*
Read a config file containing database connection information:
- Host
- Username
- Password
- Port
- Driver

`-t` *name*, `--table` *name*
The name of the table the utility will operate on.

### Snapshot Management

`-s` *|name|*, `--snapshot` *|name|*
Create snapshot of the current table state with the given name. 
Uses a combination of table name current system time if no name is provided.


### Diffing

*`|snapshot1|`* *`|snapshot2|`*
If no parameter is provided, the tool creates a diff between the last known snapshot and the current table.
If only *snapshot1* is provided, the tool writes a diff of *snapshot1* and the current table.
If *snapshot2* is provided, a diff of *snapshot1* and *snapshot2* gets written.

### IO Operations

`-o` *file*, `--output` *file*
Write diff output to file.

`-i` *file*, `--input` *file*
Write content of a diff *file* to *std::out*.
*file* can be the name of a stored output in the *out* directory, or a filepath to an output file.

`--prev`
Show last saved output (from `.out` directory).

`-l` *filetype*
List all known configs, outfiles or snapshots.
The possible values of *filetype* are "configs", "outfiles", and "snapshots".

### Filter Options for v1.1

`-s`, `--size`
Only display amount of records affected.

`-c` *c1 |c2| ...*, `--columns` *c1 |c2| ...*
Only display the specified column[s], regardless if the value has changed or not.

`--include-columns` *c1* *|c2|* *|c3| ...*
Only display records with changes in the specified column[s].

`--ignore-columns` *c1* *|c2|* *|c3| ...*
Ignore records with changes in only the specified column[s].

`-f` *condition*, `--filter` *condition*
Constrain which records should be shown (e.g. range of IDs).

`-a`, `--added`
Only show newly created records.

`-d`, `--deleted`
Only show deleted records.

`-ch`, `--changed` 
Only show records that existed previously and have changed.