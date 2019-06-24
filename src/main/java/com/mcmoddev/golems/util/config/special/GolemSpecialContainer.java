package com.mcmoddev.golems.util.config.special;

public class GolemSpecialContainer<K> {

	public String name;
	public K value;

	protected String comment;

	private GolemSpecialContainer(String lId, K defaultValue, String lComment) {
		this.name = lId;
		this.value = defaultValue;
		this.comment = lComment;
	}


	/**
	 * Fills the config options in a given builder
	 */

	@SuppressWarnings("unused")
	public static class Builder<K> {
		private final String id;
		private final K defaultValue;

		private String comment = "";

		public Builder(String name, K defaultValue) {
			this.id = name;
			this.defaultValue = defaultValue;
		}

		public Builder(String name, K defaultValue, String comment) {
			this(name, defaultValue);
			this.setComment(comment);
		}

		public Builder<K> setComment(String comment) {
			this.comment = comment;
			return this;
		}

		public GolemSpecialContainer<K> build() {
			return new GolemSpecialContainer<>(id, defaultValue, comment);
		}


	}
}
