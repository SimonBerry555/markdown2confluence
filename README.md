# Markdown2Confluence

There is a higher chance of documentation being kept up to date if it is kept with the code in the 
repository with the added benefit of having the history. However, it is not always accessible to those who need
to see it in the repository so it should also be published to another medium to allow access for a wider audience.

There is a gradle plugin which can be configured to publish markdown files to Confluence see
 [markdown-confluence-gradle-plugin](https://github.com/qwazer/markdown-confluence-gradle-plugin). 

The above is only available for gradle based projects, Markdown2Confluence will bridge the gap and allow non gradle based projects to
publish markdown documentation to Confluence.

## Configuration

You need a file called _md2confluence.yaml_ with contents similar to below:

```yaml
 restApiUrl: 'https://projectcyclone.atlassian.net/wiki/rest/api'
 spaceKey: YourConfluenceSpace
 sslTrustAll: true
 
 pages:
   - parentTitle: Top Level Page Title
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
    export CONFLUENCE_USER=you@yourcompany.com
    export CONFLUENCE_API_KEY=your_api_token
    ```

1. Run the application

    ```shell script
    java -jar markdown2confluence.jar
    ```