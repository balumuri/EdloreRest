package com.edlore.util;

import java.util.List;

public class ListOfAsset {
	private String folderName;
	private List<Asset> listAsset;

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public List<Asset> getListAsset() {
		return listAsset;
	}

	public void setListAsset(List<Asset> listAsset) {
		this.listAsset = listAsset;
	}

	@Override
	public String toString() {
		return " [folderName=" + folderName + ", listAsset="
				+ listAsset + "]";
	}

}
