# LetsLunch
An Android app for happier lunchin'.

### Useful links
https://guides.github.com/features/issues/ - Brief introduction to using issues in GitHub
https://help.github.com/articles/closing-issues-via-commit-messages/ - How to connect issues to commits/closing issues.

### Git guidelines
1. Commit often. Do one thing, then commit the change, before doing one more thing.
2. Make sure everything works before commiting, and especially before pushing. 
3. A "thing" can be small or large, and may affect multiple files. Try to keep them as small as possible, while following p.2.


#### Commit style ####
Template:

**type(scope): subject**

Type is one of the following:

Type  | Meaning
------------- | -------------
feat  | a new feature
fix  | a bugfix
docs  | documentation change (including comments)
style  | minor cleanup/code style enforcement
refactor  | larger changes, refactoring of code or structure
test  | added tests
asset | added an asset of some kind (textures, audio etc)

Scope is the 'where' of the commit. This can be a specific class, or some abstract structure (like commands), or misc if it's all over the place.
Subject is a short message about what was done.

**Examples:**

feat(entities.components): added camera handling 

fix(selectioncommand): fixed zero-size selections (issue #56)

refactor(units): units are now entities, holding entities.components which do things
