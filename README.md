# dbdiff

A command line tool to create diffs between database snapshots.

## Feature Roadmap

### v1.0 Minimum Viable Product

- Connect to SQL based database engines using a configuration file.
- Create (named) snapshots of table contents.
- Diff creation of two snapshots.
- Diff creation of a snapshot against current database table.
- Show all records that differ between snapshots.

### v1.1 Quality of Life Features
- Filter to only show added, deleted, or modified records.
- Select or ignore columns when creating diffs (filter actual diff detection).
- Select or ignore columns when displaying diffs (filter output only).
- Save and export diff results (e.g. to JSON).
- Snapshot and saved file management (list, name, delete ...)

<hr>

### v1.x Stretch goals

- Schema change detection
- NoSQL support
- Execution of SQL queries/scripts, creating a diff for the result.
    - Provide previews of diffs before committing the transaction.
- GUI based diff display (e.g. html)

<br><br>

## User Experience Design for v1.x

### Basic Usage

__-h, --help__
Show this help page.

__-db__ *config*, __--database__ *config*
Read a config file containing database connection information.

__-t__ *name*, __--table__ *name*
The table the utility will operate on.

__-q__ *statement*, __--query__ *statement*
A query that will be executed against the database.

__-sc__ *file*, __--script__ *file*
A script that will be executed against the database.

__-nc, --no-commit__ 
Give a preview of the transaction results without comitting to the database.

### IO Operations

__-o__ *file*, __--output__ *file*
Write output to file.

__--prev__
Show last saved output.

__-i__ *file*, __--input__ *file*
Write saved output *file* to *std::out*.
*file* can be the name of a stored output in the *out* directory, or a filepath to an output file.

__-sn__ *|name|*, __--snapshot__ *|name|*
Create snapshot of the current table state with the given name. Uses systime as name if no name is provided.

__-comp__ *|snapshot1|* *|snapshot2|*, __--compare__ *|snapshot1|* *|snapshot2|*
If no parameter is provided, the tool creates a diff between the last known snapshot and the current table.
If only *snapshot1* is provided, the tool writes a diff of *snapshot1* and the current table.
If *snapshot2* is provided, a diff of *snapshot1* and *snapshot2* gets written.

__-l__ *filetype*
List all known configs, outfiles or snapshots.
The possible values of *filetype* are "configs", "outfiles", and "snapshots".

### Filter Options

__-s__, __--size__
Only display amount of records affected.

__-c__ *c1 |c2| ...*, __--column__ *c1 |c2| ...*
Only display the specified column[s].

__--only-columns__ *c1* *|c2|* *|c3| ...*
Only display records with changes in the specified column[s].

__--ignore-columns__ *c1* *|c2|* *|c3| ...*
Ignore records with changes in only the specified column[s].

__-f__ *condition*, __--filter__ *condition*
Constrain which records should be shown (e.g. range of IDs).

__-a__, __--added__
Only show newly created records.

__-d__, __--deleted__
Only show deleted records.

__-ch__, __--changed__ 
Only show records that existed previously and have changed.

__-sch__, __--ignore_schema__
Ignores schema changes when creating the diff.