package com.kelsos.mbrc.data.library;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kelsos.mbrc.data.db.CacheDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "genre",
    "count"
})
@Table(name = "genre", database = CacheDatabase.class)
public class Genre extends BaseModel {

  @JsonIgnore
  @Column
  @PrimaryKey(autoincrement = true)
  private long id;

  @JsonProperty("genre")
  @Column
  private String genre;

  @JsonProperty("count")
  @Column
  private int count;

  @JsonProperty("genre")
  public String getGenre() {
    return genre;
  }

  @JsonProperty("genre")
  public void setGenre(String genre) {
    this.genre = genre;
  }

  @JsonProperty("count")
  public int getCount() {
    return count;
  }

  @JsonProperty("count")
  public void setCount(int count) {
    this.count = count;
  }

  @Override
  public String toString() {
    return "Genre{" +
        "id=" + id +
        ", genre='" + genre + '\'' +
        ", count=" + count +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Genre genre1 = (Genre) o;

    if (id != genre1.id) {
      return false;
    }
    if (count != genre1.count) {
      return false;
    }
    return genre.equals(genre1.genre);
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + genre.hashCode();
    result = 31 * result + count;
    return result;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
}

