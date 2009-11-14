#! /bin/sh

echo "-----------------------------"
echo "Reverting modified files"
echo "-----------------------------"

# Nettoyage
if [ -d META-INF/.svn ]; then
	echo "Suppression du MANIFEST modifie"
	svn revert META-INF/MANIFEST.MF
fi

if [ -d resources/.svn ]; then
	echo "Suppression des descripteurs XML"
	svn revert --recursive resources
fi

if [ -d .svn ]; then
	echo "Suppression des fichiers generes"
	svn revert --recursive .
fi

if [ `svn stat | wc -l` -gt 0 ]; then
	echo "Suppression des fichiers non versionnes"
	svn status --no-ignore | grep '^\?' | sed 's/^\?      //'
	svn status --no-ignore | grep '^\?' | sed 's/^\?      //'  | xargs rm -rf
fi

echo "Cleaning complete..."