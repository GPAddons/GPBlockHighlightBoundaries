#!/bin/bash

# Suppress unassigned values - we set from properties file.
# shellcheck disable=SC2154
# Also suppress inspection that's problematic with niche use cases (i.e. printf -v to set variables from properties file).
# shellcheck disable=SC2086

# Load properties.
IFS="="
while read -r key value
do
  printf -v $key "%s" "$value"
done < setup.properties
unset IFS

# Convert name to lower case for artifact ID.
artifactId="$(echo $name | tr '[:upper:]' '[:lower:]')"

echo "name=$name, artifactId=$artifactId"

# Replace values in pom.
sed -i "s/genericartifact/$artifactId/g; s/GenericAddon/$name/g; s/GenericDescription/$description/g; s/GenericAuthor/$author/g" ../pom.xml

# Replace values in appveyor configuration.
sed -i "s/GenericAddon/$name/g" ../appveyor.yml

# Replace values in JavaPlugin, rewriting to new location.
basedir="../src/main/java/com/github/gpaddons"
sed -i -u "s/genericartifact/$artifactId/g; s/GenericAddon/$name/g" "$basedir/genericartifact/GenericAddon.java" > "$basedir/$artifactId/$name.java"

# Delete old package. If you made the new name GenericArtifact you're a big jerk.
rm -rf $basedir/genericartifact

echo Setup complete! Delete scripts folder at your convenience.
