Documentation
=============

Steps after creation:

# Documentation

Update this file with content about the experiment.

# Git init

Then init the Git repository:


    cd existing_folder
    git init
    git remote add origin git@gitlab.com:humanfork/experiments/experiment-review-example.git
    git add .
    git commit -m "Initial commit"
    git push -u origin master
    
If https instead of ssl is used, then replace `git remote add origin git@gitlab.com...` by

    git remote add origin  https://gitlab.com/humanfork/experiments/experiment-review-example.git

If you use an different Git account with an different configuration

    git config user.name "Ralph Engelmann"
    git config user.email "ralph@rshc.de"
    
The Maven generated files will have system default EOL (Windows `CR` `LF`) but the repository enforce `LF`.
Therefore create a new workspace (for example by cloning again) or execute these commands:

    git rm --cached -r . 
    git reset --hard


or run

    gitInitRepo.bat
    
that will execute the commands above.



The generated project will have a GitLab CI file, that run `mvn test`, based on https://gitlab.com/humanfork/experiments/simple-experiment-pipeline.
> **NOTE**
> Add these settings to the badge configuration of the generated project's GitHub repository to display a pipeline badge:
> Under: `Preferences` > `General` > `Badges`
> - Name: `Pipeline` 
> - Link: `https://gitlab.com/%{project_path}/-/commits/%{default_branch}`
> - Badge image Url: `https://gitlab.com/%{project_path}/badges/%{default_branch}/pipeline.svg`
> (leave the place holders as they are)
For more details see [simple-experiment-pipeline](https://gitlab.com/humanfork/experiments/simple-experiment-pipeline)
