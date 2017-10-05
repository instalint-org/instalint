package io.instalint.daemon;

import java.security.SecureRandom;
import java.util.regex.Pattern;

class RandomString {

  private static final int LENGTH = 16;

  private static final char[] symbols = {
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
  };
  private static final Pattern result = Pattern.compile("^[a-zA-Z0-9]{" + LENGTH + "}$");

  private static final ThreadLocal<SecureRandom> RANDOM = ThreadLocal.withInitial(SecureRandom::new);

  private RandomString() {
  }

  static String generate() {
    char[] buf = new char[LENGTH];
    SecureRandom random = RANDOM.get();
    for (int i = 0; i < LENGTH; i++) {
      buf[i] = symbols[random.nextInt(symbols.length)];
    }
    return new String(buf);
  }

  static void validate(String uuid) {
    if (!result.matcher(uuid).matches()) {
      throw new IllegalStateException("Invalid storedAs: " + uuid);
    }
  }
}
