define ["jquery", "t5/core/dom"],
	($, dom) ->
		PANEL_ANIMATION_DELAY = 20 #ms
		PANEL_ANIMATION_STEPS = 10
		PANEL_NORMAL_CLASS    = 'collapsiblepanel_content'
		PANEL_COLLAPSED_CLASS = 'collapsiblepanel_content collapsiblepanel_content_collapsed'


		# Start the expand/collapse animation of the panel
		# @param panel reference to the panel div
		animateContentPanel = (panelContent, expanding) ->
			# make sure the content is visible before getting its height
			panelContent.style.display = "block";

			# get the height of the content
			contentHeight = panelContent.offsetHeight;

			# if panel is collapsed and expanding, we must start with 0 height
			panelContent.style.height = "0px" if expanding

			stepHeight = contentHeight / PANEL_ANIMATION_STEPS

			animateStep panelContent,1,stepHeight,expanding
			return

		animateStep = (panelContent, iteration, stepHeight, expanding) ->
			if iteration<PANEL_ANIMATION_STEPS
				panelContent.style.height = "#{Math.round( (if expanding then iteration else PANEL_ANIMATION_STEPS - iteration) * stepHeight)}px"
				iteration++
				setTimeout((-> animateStep(panelContent,iteration,stepHeight,expanding)), PANEL_ANIMATION_DELAY)
			else
				# set class for the panel
				panelContent.className = if expanding then PANEL_NORMAL_CLASS else PANEL_COLLAPSED_CLASS
				# clear inline styles
				panelContent.style.display = panelContent.style.height = ""
			return

		dom.onDocument "click", "span[data-action=collapsible]", ->
			contentElement = this.element.parentNode.nextSibling
			expanding = contentElement.className.indexOf('collapsed') > 0
			this.element.className = if expanding then 'collapsiblepanel_header_title collapsiblepanel_header_title_expanded' else 'collapsiblepanel_header_title collapsiblepanel_header_title_collapsed'

			animateContentPanel contentElement, expanding
			# alert "hello world"
		return
