package guru.qa.rococo.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Color {
  red("rgba(143, 15, 34, 1)"), yellow("rgba(230, 200, 51, 1)");
  public final String rgb;
}
