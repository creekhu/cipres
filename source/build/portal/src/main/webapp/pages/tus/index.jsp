<%@ taglib prefix="s" uri="/struts-tags" %>
<head>
    <title>Resumable File Upload</title>
    <content tag="menu">Resumable File Upload</content>

	<!--
	We're using bootstrap 3, included via decorator default.jsp, in the rest of the portal app,
	but the tus-js code is written for bootstrap 2.3.1.  I don't know bootstrap well enough to easily
	modify the tus code to use the newer version.  If I don't include 2.3.1 the text on the browse 
	and abort upload buttons disappears.
	-->
    <link href="https://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css" rel="stylesheet" />
    <link href="./pages/tus/demo.css" rel="stylesheet" media="screen" />
</head>

  <body>
  <s:url id="uploadServlet" value="/files/"/>	

    <div class="container">
      <h1>Resumable File Upload</h1>

      <div class="alert alert-warining hidden" id="support-alert">
        <b>Warning!</b> Your browser does not seem to support the features necessary to work with this page. The buttons below may work but probably will fail silently.
      </div>

      <br />

	  <input type="hidden" id="endpoint" name="endpoint" value='<s:property value="%{uploadServlet}"/>' />
      <table>
        <tr>
          <td>
            <label for="chunksize">
              Chunk size (bytes):
            </label>
          </td>
          <td>
            <input type="number" id="chunksize" name="chunksize">
          </td>
        </tr>
        <tr>
          <td>
            <label for="resume">
              Perform full upload:
              <br />
              <small>(even if we could resume)</small>
            </label>
          </td>
          <td>
            <input type="checkbox" id="resume">
          </td>
        </tr>
      </table>
      
      <br />

      <input type="file">

      <br />
      <br />

      <div class="row">
        <div class="span8">
          <div class="progress progress-striped progress-success">
            <div class="bar" style="width: 0%;"></div>
          </div>
        </div>
        <div class="span4">
          <button class="btn btn-danger stop disabled" id="stop-btn"><i class="icon-white icon-stop"></i> abort upload</button>
        </div>
      </div>

      <hr />
      <h3>Uploads</h3>
      <p>
        Succesful uploads will be listed here. 
		<ul id="list"></ul>
      </p>

    </div>
	<script src="<s:url value='/pages/tus/tus.js'/>"></script>
	<script src="<s:url value='/pages/tus/demo.js'/>"></script>
  </body>

