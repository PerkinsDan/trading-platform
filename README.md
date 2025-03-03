# trading-platform

A trading platform for our group assignment (Software Engineering Tools Techniques and Practices)

- Each separate microservice should be created in apps/ with its own pom.xml
- Any shared libraries / utilities can be put in packages/
- scripts/ is for CICD scripts

On Mac?
You may run into:

```zsh
hint: The '.husky/pre-commit' hook was ignored because it's not set as executable.
hint: You can disable this warning with `git config advice.ignoredHook false`.
```

In this case run:

```zsh
$ chmod ug+x .husky/*
(This makes the pre-commit file executable and you can rerun the git commit)
```
