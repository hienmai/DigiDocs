PrimeFaces.widget.Chips = PrimeFaces.widget.Chips
		.extend({
			isValid : function(value) {
				var arrChip = [];
				$("li.ui-chips-token").map(function() { arrChip.push($(this).text().trim()) });
				if (value.trim() != '') {
					var exists = false;
					for (var i = 0; i < arrChip.length; i++) {
						if (value.trim() === arrChip[i]) {
							exists = true;
							$('#tooltip_tag').stop().fadeIn(100).fadeOut(2500);
							break;
						}
					}
					return !exists;
				}
				return false;
			},

			bindEvents : function() {
				var $this = this;

				this.itemContainer.hover(function() {
					$(this).addClass('ui-state-hover');
				}, function() {
					$(this).removeClass('ui-state-hover');
				}).click(function() {
					$this.input.focus();
				});

				this.input
						.on('focus.chips', function() {
							$this.itemContainer.addClass('ui-state-focus');
						})
						.on('blur.chips', function() {
							$this.itemContainer.removeClass('ui-state-focus');
						})
						.on(
								'keydown.chips',
								function(e) {
									var value = $(this).val().toLowerCase().replace(/ +(?= )/g,'').trim();

									switch (e.which) {
									// enter save tag
									case 13:
										// if tagName is exist
										if ($('#currentIdDocument').text() == '') {
											$('#chips_input').val('');
											break;
										}
										if ($this.isValid(value)
												&& value
												&& value.trim().length
												&& (!$this.cfg.max || $this.cfg.max > $this.hinput
														.children('option').length)) {
											
											createTag([ {
												name : 'name',
												value : value
											} ]);
											$this.addItem(value);
											$(".ui-chips-container")
													.mCustomScrollbar("update");
										}
										e.preventDefault();
										break;

									default:
										if ($this.cfg.max
												&& $this.cfg.max === $this.hinput
														.children('option').length) {
											e.preventDefault();
										}
										break;
									}
								});

				var closeSelector = '> li.ui-chips-token > .ui-chips-token-icon';
				this.itemContainer.off('click', closeSelector).on('click',
						closeSelector, null, function(event) {
							// $this.removeItem($(this).parent());
						});
			}
		});
// Delete tag
$(document).on('click', 'li.ui-chips-token > .ui-chips-token-icon', function() {
	var nameTagDelete = $(this).parent().children('.ui-chips-token-label').text();
	deleteTag([ {
		name : 'name',
		value : nameTagDelete
	} ]);
	
	$(this).parent().fadeOut(300, function() {
        var token = $(this);
        token.remove();
    });
	
	$('#chips_input').blur();
});

$(document).ready(function() {
	/* copy right year */
	var date = new Date();
	var year = date.getFullYear();
	$("span.copyright-year").html(year);
	// setHeight($("div.ui-datatable-scrollable-body"));
	/* remove on-click action materialize added */
	$('button').prop('onclick', null);
	/* update text field of materialize */
	Materialize.updateTextFields();
});

$(document).ready(function() {
	// disable input tag if file id not exist
	$('#chips_input').prop('disabled', true);
	disableInputText();
	$('.sidenav').sidenav();
	$(".sidebar .list-folder").css("max-height", $(window).height() - 165);
	$('#list-hide').css("max-height", $(window).height() - 115);

	selectOldOrNewFile();
	setTimeout(showOrHideScrollBarInLeftSidebar, 300);
	setTimeout(showOrHideScrollBarInSmallDevice, 300);
	showLocationFileFromURL();
	$('a[href*="?id="]').hide();
	setWidthForContainerTag();
	showOrHideTagListScrollBar();
	addScrollBarToTagList();
	//check file id exist
	checkFileIdExist();
});

function setWidthForContainerTagInBigScreen() {
	var widthIframe = parseInt($('iframe').width());
	widthIframe -= 29;
	$('#chips ul.ui-chips-container').css('width', widthIframe);
}

function setWidthForContainerTagInSmallScreen() {
	var widthIframe = parseInt($('iframe').width());
	widthIframe -= 66;
	$('#chips ul.ui-chips-container').css('width', widthIframe);
}

function setWidthForContainerTag() {
	if($(window).width() > 1024) {
		setWidthForContainerTagInBigScreen();
	} else {
		setWidthForContainerTagInSmallScreen();
	}
}

// Event update container tag
$("#content").on("DOMNodeInserted", '.view', function() {
	setWidthForContainerTag();
	addScrollBarToTagList();
	$("#chips_input").attr("placeholder", " +");
	checkFileIdExist();
});

function checkFileIdExist() {
	if($('#currentIdDocument').text() != '') {
		$('#chips_input').prop('disabled', false);
	}
}

function showOrHideScrollBarInSmallDevice() {
	var heightFoldersCurrent = parseInt($('#list-hide').find(
			'.list-folder-hide').height());
	heightFoldersCurrent += 35; // add 35 of padding of #list-hide
	var maxHeight = parseInt($('#list-hide').css('max-height'));
	// alert("current "+heightFoldersCurrent + "max "+maxHeight);
	if (heightFoldersCurrent >= maxHeight) {
		$('#list-hide').css('overflow', 'scroll');
		$('#list-hide').css('overflow-x', 'hidden');
	} else {
		$('#list-hide').css('overflow', 'hidden');
	}
}

function showOrHideScrollBarInLeftSidebar() {
	var heightFoldersCurrent = parseInt($('#folders').height());
	var paddingOfFolders = parseInt(($('#folders').css('padding-bottom'))
			.split('p')[0]);
	var maxHeight = parseInt($('#folders').css('max-height'));
	maxHeight -= paddingOfFolders;
	maxHeight -= 30;
	if (heightFoldersCurrent >= maxHeight) {
		$('.list-folder').css('overflow', 'scroll');
		$('.list-folder').css('overflow-x', 'hidden');
	} else {
		$('.list-folder').css('overflow', 'hidden');
	}
}

// Event on every CARD
$(".card").click(function(e) {
	$('.fixed-action-btn > a > i').text("fullscreen");
	$('#form').find('iframe').removeClass('full-screen-pdf-viewer');
	// ----------------------------------------------
	var card = $(this);
	// get id document
	var currentDocumentId = card.attr('data-id-doc');
	if (card.hasClass('false')) {
		updateStateDocument([ {
			name : 'id',
			value : currentDocumentId
		} ]);
	}

	$('div.active-card:not([data-id-doc=' + currentDocumentId + '])')
			.removeClass('active-card');
	$('div.card.false[data-id-doc=' + currentDocumentId + ']')
			.removeClass("false");

	$("[data-id-doc=" + currentDocumentId + "]")
			.addClass("active-card");
	$(".ui-chips-container").mCustomScrollbar("update");
});

function isIconOpen(iconFolder) {
	if (iconFolder.hasClass('fa-folder-open')) {
		return true;
	}
	return false;
}

function isIconClose(iconFolder) {
	if (iconFolder.hasClass('fa-folder')) {
		return true;
	}
	return false;
}

function changeIcon(iconFolder) {
	if (isIconOpen(iconFolder)) {
		iconFolder.attr('class', 'fa fa-folder');
		return false;
	} else {
		closeCardsOnPreFolder();
		changeAllIconOpenToClose();
		// only open is selected
		iconFolder.attr('class', 'fa fa-folder-open');
	}
}

function changeAllIconOpenToClose() {
	$('i.fa.fa-folder-open').attr('class', 'fa fa-folder');
}

function closeCardsOnPreFolder() {
	var folderYear = $('.fa-folder-open').parent().parent();
	$(folderYear).children('.cards').hide();
}

// Click name folder year
$('.folder-link.year').click(
		function(event) {
			var $this = $(this);
			// current year select
			var dataIdYear = $(this).parent().attr('data-id-year');
			var iconFolder = $(this).children('.fa');

			// is icon folder closing
			if (isIconClose(iconFolder)) {
				$('.cards').removeClass('active').hide();

				console.log($('div[data-id-year = ' + dataIdYear + ']')
						.children('.cards').length);
				$('div[data-id-year = ' + dataIdYear + ']').children('.cards')
						.addClass('active').slideDown(100);
			} else { // is opening
				$('div[data-id-year = ' + dataIdYear + ']').children('.cards')
						.removeClass('active').slideUp(170);
			}
			changeIcon(iconFolder);
			setTimeout(showOrHideScrollBarInLeftSidebar, 110);
			setTimeout(showOrHideScrollBarInSmallDevice, 110);
			return false;
		});

// --------Click card in sidebar hide and hide sidebar if not the same file
$('#list-hide').find('.card').click(function() {
	setTimeout(function() {
		$('#slide-out').find('.sidenav-close').click();
	}, 300);
});

// -----Change size screen
$(window).resize(function() {
	$(".sidebar .list-folder").css("max-height", $(window).height() - 165);
	$('#list-hide').css("max-height", $(window).height() - 115);
	setTimeout(showOrHideScrollBarInLeftSidebar, 300);
	setTimeout(showOrHideScrollBarInSmallDevice, 300);
	$('.input-filter').css("max-width", "290px");
	setWidthForContainerTag();
});

// Add background for new or old file
function selectOldOrNewFile() {
	var currentFileId = $('#currentIdDocument').text();

	if (currentFileId != "") {
		$('.card[data-id-doc=' + currentFileId + ']').addClass('active-card');
	}
}

// Handle button fullscreen PDF
$('.fixed-action-btn').click(function() {
	if (!($('#form').find('iframe').hasClass('full-screen-pdf-viewer'))) {
		$('#form').find('iframe').addClass('full-screen-pdf-viewer');
		$('.fixed-action-btn > a > i').text("fullscreen_exit");
	} else {
		$('#form').find('iframe').removeClass('full-screen-pdf-viewer');
		$('.fixed-action-btn > a > i').text("fullscreen");
	}
});

// close fullscreen when click ESC button
$(document).keyup(function(e) {
	var checkESCKeyPress = (e.keyCode == 27 ? 1 : 0);
	if (checkESCKeyPress) {
		if (($('#form').find('iframe').hasClass('full-screen-pdf-viewer'))) {
			$('#form').find('iframe').removeClass('full-screen-pdf-viewer');
			$('.fixed-action-btn > a > i').text("fullscreen");
		}
	}
});

function entered() {
	enter([ {} ]);
	$(".dialogEditName").close();
}

function load() {
	$('iframe')
			.ready(
					function() {
						var iframe = document.getElementById("form:show_pdf").contentWindow.document
								.getElementById("presentationMode");
						if (iframe) {
							iframe.style.display = "none";
						}
					});
}

$(window)
		.bind(
				"load",
				function() {
					var iframe = document.getElementById("form:show_pdf").contentWindow.document
							.getElementById("presentationMode");
					iframe.style.display = "none";
				});

// For edit filename
$('.fa-pencil-square-o').hover(function() {
	$(this).css("color", "#0096d6");
}, function() {
	$(this).css("color", "#777");
});

// show location folder when user enter id from URL
function showLocationFileFromURL() {
	var item = $('.active-card');
	var documentIdSelect = item.attr('data-id-doc');

	if (item.length != 0) {
		$('.cards:not(.cards .card[data-id-doc=' + documentIdSelect + '])')
				.removeClass('active').hide();
		var cards = $('.card[data-id-doc=' + documentIdSelect + ']').parent(
				'.cards');
		cards.addClass('active');
		cards.slideDown(200);
		cards.prev('a.year').children('i.fa')
				.attr('class', 'fa fa-folder-open');
	}
}

// For disable Salt Key
function disableInputText() {
	var input = $('.salt-key-input');
	var button = $('#buttons button:last-child');
	if (input.val()) {
		input.prop("disabled", true);
		button.attr("rendered", true).addClass('ui-state-disabled');
	}
}

function addScrollBarToTagList() {
	$(".ui-chips-container").mCustomScrollbar({
		scrollButtons : {
			enable : false
		},
		axis : "x",
		setTop : 0
	});
}

// Add placeholder for input tag
$("#chips_input").attr("placeholder", " +");

function showOrHideTagListScrollBar() {
	var maxHeight = $(window).height();
	maxHeight -= 125
	maxHeight -= 35
	var tagListHeight = parseInt($('#list-hide-tag').height());
	if (tagListHeight >= maxHeight) {
		$('#list-hide-tag').css('overflow-y', 'scroll')
	} else {
		$('#list-hide-tag').css('overflow-y', 'hidden')
	}
}