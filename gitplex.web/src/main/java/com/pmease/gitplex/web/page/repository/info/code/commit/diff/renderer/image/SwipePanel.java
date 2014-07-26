package com.pmease.gitplex.web.page.repository.info.code.commit.diff.renderer.image;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;

import com.google.common.base.Strings;
import com.pmease.gitplex.web.page.repository.info.code.blob.renderer.FileBlobImage;
import com.pmease.gitplex.web.page.repository.info.code.commit.diff.patch.FileHeader;

import de.agilecoders.wicket.jquery.JQuery;

@SuppressWarnings("serial")
public class SwipePanel extends AbstractImageDiffPanel {

	public SwipePanel(String id, IModel<FileHeader> fileModel,
			IModel<String> sinceModel,
			IModel<String> untilModel) {
		
		super(id, fileModel, sinceModel, untilModel);
	}

	Image oldImage;
	Image newImage;
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		FileHeader file = getFile();
		String since = getSince();
		String until = getUntil();
		
		if (file.getChangeType() == ChangeType.ADD || Strings.isNullOrEmpty(since)) {
			add(new WebMarkupContainer("old").setVisibilityAllowed(false));
		} else {
			add(oldImage = new FileBlobImage("old", 
					getRepository(), 
					since,
					file.getOldPath()));
		}
		
		if (file.getChangeType() == ChangeType.DELETE) {
			add(new WebMarkupContainer("new").setVisibilityAllowed(false));
		} else {
			add(newImage = new FileBlobImage("new",
					getRepository(),
					until,
					file.getNewPath()));
		}
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(JavaScriptHeaderItem.forReference(TwentyTwentyResourceReference.getInstance()));
		response.render(OnDomReadyHeaderItem.forScript(getInitScript()));
	}
	
	private String getInitScript() {
		String str = 
				"gitplex.utils.onImageLoad('#" + newImage.getMarkupId(true) + "', function() {"
				+ "gitplex.utils.onImageLoad('#" + oldImage.getMarkupId(true) + "', function() {"
				+ JQuery.$(this, ".swipe-view").chain("twentytwenty").get()
				+ "});});";
		
		return str;
	}
}
