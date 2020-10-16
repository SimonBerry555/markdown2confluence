# Markdown2Confluence

To keep documentation accessible it should be published to Confluence and to make it easier to keep it
up to date it should probably be located with the code i.e. in markdown files in the repository.

There is a gradle plugin which can be configured to publish markdown files to Confluence see [markdown-confluence-gradle-plugin](https://github.com/qwazer/markdown-confluence-gradle-plugin). 

Many VIOOH gradle based projects are using this and the published documentation can be seen in the VSD Confluence space, this way of publishing is
supported by helmut which will detect the `confluence` configuration in the build.gradle file and run that task.

The above is only available for gradle based projects, Markdown2Confluence will bridge the gap and allow non gradle based projects to
publish markdown documentation to Confluence.

Eventually this will be integrated with helmut but at the moment it is a manual process, please follow the steps below:

## Configuration

You need a file called _md2confluence.yaml_ with contents similar to below:

```yaml
 restApiUrl: 'https://projectcyclone.atlassian.net/wiki/rest/api'
 spaceKey: VSD
 sslTrustAll: true
 
 pages:
   - parentTitle: Component/Service Documentation
     title: Markdown to Confluence
     filepath: README.md
   - parentTitle: Markdown to Confluence
     title: Markdown Test Page
     filepath: TestPage.md
```

## Running the Application

1. Retrieve the application executable jar:
    
    ```shell script
   wget -O markdown2confluence.jar $(curl -s https://api.github.com/repos/SimonBerry555/markdown2confluence/releases/latest | jq -r '.assets[0].browser_download_url')
    ```

1. Get a confluence API token from here: 
    
    [https://id.atlassian.com/manage-profile/security/api-tokens](https://id.atlassian.com/manage-profile/security/api-tokens), login and create your token (keep it somewhere safe)
    
1. Set your environment

    ```shell script
    export CONFLUENCE_USER=you@viooh.com
    export CONFLUENCE_API_KEY=your_api_token
    ```

1. Run the application

    ```shell script
    java -jar markdown2confluence.jar
    ```