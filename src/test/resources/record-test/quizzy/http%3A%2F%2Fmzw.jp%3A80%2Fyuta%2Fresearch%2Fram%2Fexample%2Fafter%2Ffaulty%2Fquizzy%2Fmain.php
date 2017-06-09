<html>
<head>
<script type="text/javascript" src="quizzy/lib/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="quizzy/lib/jquery.loading.min.js"></script>
<script type="text/javascript" src="quizzy/quizzyConfig.js" charset="utf-8"></script>
<script type="text/javascript" src="quizzy/quizzy.js" charset="utf-8"></script>
<link rel="stylesheet" type="text/css" href="quizzy/quizzy.css" charset="utf-8">
<link rel="stylesheet" type="text/css" href="quizzy/quizzySkin.css" charset="utf-8">
<!-- overflow:hidden in IE is currently breaking mask calcs :( -->
<!--[if IE]>
  <style type="text/css">
    .loading-masked { overflow: visible; }
    /*required to make overflow hidden for quizzy in ie 6/7*/
    #quizzy {position:relative;}
  </style>
<![endif]-->
<!--[if lt IE 7]>
  <style type="text/css">
    /*Max width and height are not suppored by ie 6 so for those browsers, we're just going
      to set the images' width and height to the max percentages.
    */
    #quizzy_load img{ width: 30%; }
    .quizzy_q_body img {width: 45%; }
    .quizzy_q_opts img {height: 12pt;}
    .quizzy_q_exp img {width: 45%; height: auto;}
    .quizzy_done img {height: 15%;}
  </style>
<![endif]--> 
</head>
<body>
<img src="ase16.png" width="200px" />
<script type="text/javascript">
  quizWidth = 200;
  quizHeight = 300;
</script>
<div id="quizzy" style="width: 200px; height: 300px">
	<div id="quizzy_c" style="width: 600px">
		<div id="quizzy_load" style="width: 200px"></div>
		<div id="quizzy_quiz" style="width: 600px"></div>
	</div>
</div>
 
</body>
</html>
