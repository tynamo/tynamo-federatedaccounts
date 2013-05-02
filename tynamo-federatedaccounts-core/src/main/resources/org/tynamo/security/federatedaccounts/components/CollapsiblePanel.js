function toggleCollapsiblePanel(element) {
	// it's expected that the header element is passed as an argument
  var contentElement = element.parentNode;
	do contentElement = contentElement.nextSibling;
	while (contentElement && contentElement.nodeType != 1);

	var expanding = element.className.indexOf('collapsed') > 0;
	element.className = expanding ? 'collapsiblepanel_header_title collapsiblepanel_header_title_expanded' : 'collapsiblepanel_header_title collapsiblepanel_header_title_collapsed';
	if (contentElement == element) return;

	var panelAnimator = new function() {
		this.PANEL_ANIMATION_DELAY = 20; /*ms*/
		this.PANEL_ANIMATION_STEPS = 10;
		this.PANEL_NORMAL_CLASS    = 'collapsiblepanel_content';
		this.PANEL_COLLAPSED_CLASS = 'collapsiblepanel_content collapsiblepanel_content_collapsed';

		/**
		 * Start the expand/collapse animation of the panel
		 * @param panel reference to the panel div
		 */
		this.animateContentPanel = function(panelContent, expanding) {
			// make sure the content is visible before getting its height
			panelContent.style.display = "block";

			// get the height of the content
			var contentHeight = panelContent.offsetHeight;

			// if panel is collapsed and expanding, we must start with 0 height
			if (expanding)
				panelContent.style.height = "0px";

			var stepHeight = contentHeight / this.PANEL_ANIMATION_STEPS;
			var direction = expanding ? 1 : -1;

			setTimeout(function(){panelAnimator.animateStep(panelContent,1,stepHeight,direction)}, this.PANEL_ANIMATION_DELAY);
		}

		/**
		 * Change the height of the target
		 * @param panelContent	reference to the panel content to change height
		 * @param iteration		current iteration; animation will be stopped when iteration reaches PANEL_ANIMATION_STEPS
		 * @param stepHeight	height increment to be added/substracted in one step
		 * @param direction		1 for expanding, -1 for collapsing
		 */
		this.animateStep = function(panelContent, iteration, stepHeight, direction) {
			if (iteration<this.PANEL_ANIMATION_STEPS) {
				panelContent.style.height = Math.round(((direction>0) ? iteration : 10 - iteration) * stepHeight) +"px";
				iteration++;
				setTimeout(function(){panelAnimator.animateStep(panelContent,iteration,stepHeight,direction)}, this.PANEL_ANIMATION_DELAY);
			}
			else {
				// set class for the panel
				panelContent.className = (direction<0) ? this.PANEL_COLLAPSED_CLASS : this.PANEL_NORMAL_CLASS;
				// clear inline styles
				panelContent.style.display = panelContent.style.height = "";
			}
		}
	};
	panelAnimator.animateContentPanel(contentElement, expanding);
}