# this script is called when the judge wants your compiler to compile a source file.
# print the compiled source, i.e. asm code, directly to stdout.
# don't print anything else to stdout.
# if you would like to print some debug information, please go to stderr.

set -e
cd "$(dirname "$0")"
java -classpath lib/antlr-4.7.2-complete.jar:bin jwb.Main