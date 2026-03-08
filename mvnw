#!/bin/sh
if command -v mvn >/dev/null 2>&1; then
  exec mvn "$@"
fi
echo "Maven is not installed. Install Maven 3.9+ or generate a full Maven Wrapper before running this build."
exit 1
