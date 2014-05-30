package {package_name};

final class Modules {
  static Object[] list({app_class_prefix}App app) {
    return new Object[] {
        new {app_class_prefix}Module(app)
    };
  }

  private Modules() {
    // No instances.
  }
}
