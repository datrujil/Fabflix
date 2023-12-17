const metadataBtn = document.getElementById('metadata_button');

const metaTable = $('#metadata_table');

function createMetaTable (resultData) {

    const uniqueTableNames = new Set();

    // Iterate through the array
    resultData.forEach(entry => {
        // Add the table name to the set
        uniqueTableNames.add(entry.table);
    });
    // let htm = "<h2>" + uniqueTableNames[i]

    let html = "<tr>" +
        "<th>Table</th>" +
        "<th>Column</th>" +
        "<th>Data Type</th>" +
        "</tr>";

    for(let i = 0; i < resultData.length; ++i)
    {
        html += "<tr>" +
            "<td>" + resultData[i]["table_name"] + "</td>" +
            "<td>" + resultData[i]["column_name"] + "</td>" +
            "<td>" + resultData[i]["data_type"] + "</td>" +
            "</tr>>";
    }

    metaTable.append(html);
}
function showMetadata() {
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/metadata", // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => createMetaTable(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}
metadataBtn.addEventListener('click', showMetadata);



