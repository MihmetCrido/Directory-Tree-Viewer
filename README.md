# Directory Tree Viewer
This is a Java-based program that generates a visual tree-like representation of a directory structure. It includes various features to filter and sort files and directories based on specific criteria.

## Table of Contents
- [Installation](#installation)
- [Building the Project](#building-the-project)
- [Usage](#usage)
- [Command-Line Options](#command-line-options)
- [Examples](#examples)
- [Dependencies](#dependencies)

## Installation

1. Clone the repository (if you have not done so already): git clone https://github.com/MihmetCrido/Directory-Tree-Viewer.git cd directory-tree-viewer

2. Make sure you have Maven installed. You can check if Maven is installed by running: mvn -version

3. Make sure you have Java 23 installed, as the project uses features from this version. Verify your installation: java -version

## Building the Project

To build the project and create the runnable JAR file, use Maven to package it: mvn clean package

This will generate an uber JAR file in the target directory named xp.jar. This file includes all the dependencies required to run the program.

## Usage
To run the program, use the following command: java -jar target/xp.jar [options]

If no options are provided, the program will display the directory tree of the current directory.

## Command-Line Options

Here is a list of available command-line options (or use --help for more information):

```
-d,--depth <arg>          Limit the depth of the directory tree traversal
-h,--show-hidden          Show hidden files and directories
-l,--follow-links         Follow symbolic links
-m,--show-modified        Display the last modified date
-p,--path <arg>           Path to the directory (default: current
                          directory)
-s,--sort <arg>           Sort files by: name (default), size, modified,
                          or reverse
-t,--type <arg>           Filter files by type (e.g., .txt, .java)
-x,--exclude-name <arg>   Exclude files or directories by name
                          (comma-separated, e.g., 'target,.git')
-z,--show-size            Display file sizes
```
## Examples
Here are some examples of how to use the program with different options:

Display the tree of a specific directory:

`java -jar target/xp.jar -p /path/to/directory/root`

Example structure

```
<root>/
└── folder/
├── file1
├── subfolder1/
│   └── file2
└── subfolder2/
└── file3
```
Show hidden files and directories:

`java -jar target/xp.jar -h`

Exclude specific files or directories by name:

`java -jar target/xp.jar -x target,.git`

Limit the tree depth to 2 levels:

`java -jar target/xp.jar -d 2`

Sort files by size and display their sizes:

`java -jar target/xp.jar -s size -z`

Display only .java files and their containing directories:

`java -jar target/xp.jar -t .java`

Show files with their last modified date:

`java -jar target/xp.jar -m`

Follow symbolic links while traversing:

`java -jar target/xp.jar -l`

## Dependencies
The program relies on the following dependencies:

* Apache Commons CLI: For parsing command-line options.
## How to Manage Dependencies
All dependencies are managed via Maven. To add or update dependencies, modify the pom.xml file, and Maven will handle the rest.

## License
This project is licensed under the MIT License. See the LICENSE file for details.