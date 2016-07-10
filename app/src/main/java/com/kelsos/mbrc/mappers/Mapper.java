package com.kelsos.mbrc.mappers;

interface Mapper<From, To> {
  To map(From from);
}
