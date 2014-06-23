#!/usr/bin/python

import argparse
import sys
import os
import shutil
from os.path import join

class TemplateWriter():
	def __init__(self, app_name, package_name, app_class_prefix, 
		compile_sdk_version, min_sdk_version, target_sdk_version, output_folder):
		self.app_name = app_name
		self.package_name = package_name
		self.output_folder = output_folder
		self.app_class_prefix = app_class_prefix

		self.replacements = {
			'{app_name}' : app_name,
			'{package_name}' : package_name,
			'{app_class_prefix}' : app_class_prefix,
			'{compile_sdk_version}' : compile_sdk_version,
	        '{min_sdk_version}' : min_sdk_version,
	        '{target_sdk_version}' : target_sdk_version,
		}

	def __create_output_folder(self):
		self.__create_folder(self.output_folder)

	def __create_readme(self):
		f = open(join(self.output_folder, 'README.md'), 'w')
		f.write(self.app_name + '\n=========')
		f.close()

	def __create_folder_structure(self):
		self.__create_folder(join(self.output_folder, 'libs'))

		base_app_folder = self.__app_base_folder()

		base_test_folder = join(base_app_folder, 'src/androidTest/java')
		base_source_folder = join(base_app_folder, 'src/main/java')
		base_res_folder = join(base_app_folder, 'src/main/res')
		#debug_res_folder = join(base_app_folder, 'src/debug/res')
		#release_res_folder = join(base_app_folder, 'src/release/res')

		packge_path = self.package_name.split('.')

		package_folder = base_source_folder
		release_package_folder = join(base_app_folder, 'src/release/java')
		debug_package_folder = join(base_app_folder, 'src/debug/java')
		test_package_folder = base_test_folder 
		for folder in packge_path:
			package_folder = join(package_folder, folder)
			release_package_folder = join(release_package_folder, folder)
			debug_package_folder = join(debug_package_folder, folder)
			test_package_folder = join(test_package_folder, folder)


		self.__create_folder(test_package_folder)
		self.__create_folder(package_folder)
		self.__create_folder(debug_package_folder)
		self.__create_folder(release_package_folder)
		self.__create_folder(base_res_folder)
		#self.__create_folder(debug_res_folder)
		#self.__create_folder(release_res_folder)

		#self.__create_folder(join(base_app_folder, 'src/debug/res/values'))
		#self.__create_folder(join(base_app_folder, 'src/release/res/values'))

	def __copy_source_files(self):
		files = []

		base_app_folder = self.__app_base_folder()
		
		base_res_folder = join(base_app_folder, 'src/main/res/')
		base_source_folder = join(base_app_folder, 'src/main/java/')
		debug_res_folder = join(base_app_folder, 'src/debug/res/')
		debug_source_folder = join(base_app_folder, 'src/debug/java/')
		release_res_folder = join(base_app_folder, 'src/release/res/')
		release_source_folder = join(base_app_folder, 'src/release/java/')

		package_folder = base_source_folder
		packge_path = self.package_name.split('.')
		for folder in packge_path:
			package_folder = join(package_folder, folder)
		
		debug_package_folder = debug_source_folder
		debug_packge_path = self.package_name.split('.')
		for folder in debug_packge_path:
			debug_package_folder = join(debug_package_folder, folder)
		
		release_package_folder = release_source_folder
		release_packge_path = self.package_name.split('.')
		for folder in release_packge_path:
			release_package_folder = join(release_package_folder, folder)
					
		for root, dirs, files in os.walk('template/App/debug/'):
			for d in dirs:
				if d == 'res_folders':
					self.__copytree(join(root, d), debug_res_folder)
				elif d == 'source_folders':
					self.__copytree(join(root, d), debug_package_folder)
					
			for f in files:
				filepath = join(root, f)
				if filepath == 'template/App/debug/AndroidManifest.xml':
					shutil.copyfile(filepath, join(join(base_app_folder, 'src/debug'), f))
					
		for root, dirs, files in os.walk('template/App/main/'):
			for d in dirs:
				if d == 'res_folders':
					self.__copytree(join(root, d), base_res_folder)
				elif d == 'source_folders':
					self.__copytree(join(root, d), package_folder)
					
			for f in files:
				filepath = join(root, f)
				if filepath == 'template/App/main/AndroidManifest.xml':
					shutil.copyfile(filepath, join(join(base_app_folder, 'src/main'), f))
					
		for root, dirs, files in os.walk('template/App/release/'):
			for d in dirs:
				if d == 'res_folders':
					self.__copytree(join(root, d), release_res_folder)
				elif d == 'source_folders':
					self.__copytree(join(root, d), release_package_folder)
					
		for root, dirs, files in os.walk('template/'):	
			for f in files:
				filepath = join(root, f)
				if filepath in ['template/.gitignore', 'template/build.gradle', 'template/settings.gradle']:
					self.__copy_files(self.output_folder, {f})
				elif filepath == 'template/App/build.gradle':
					shutil.copyfile(filepath, join(base_app_folder, f))
				elif filepath == 'template/App/release_strings.xml':
					#print('GOT release strings')
					shutil.copyfile(filepath, join(base_app_folder, 'src/release/res/values/strings.xml'))
				elif filepath == 'template/App/debug_strings.xml':
					#print('GOT debug')
					shutil.copyfile(filepath, join(base_app_folder, 'src/debug/res/values/strings.xml'))

		#Rename android.app.Application Subclass
		os.rename(join(debug_package_folder, 'DebugAppModule.java'), join(debug_package_folder, 'Debug' + self.app_class_prefix + 'Module.java'))
		#Rename android.app.Application Subclass
		os.rename(join(package_folder, 'App.java'), join(package_folder, self.app_class_prefix + 'App.java'))
		#Rename android.app.Application Subclass
		os.rename(join(package_folder, 'Module.java'), join(package_folder, self.app_class_prefix + 'Module.java'))

	def __create_folder(self, folder_path):
		if not os.path.exists(folder_path):
			print ('Creating folder ' + folder_path)
			os.makedirs(folder_path)

	def __run_replacement(self):
		for root, dirs, files in os.walk(self.output_folder):
			for filename in files:
				if not filename.endswith('.png'): #exclude files that should not be opened
					fname = join(root, filename)
					contents = open(fname).read()
					out = open(fname, 'w')
					for i in self.replacements.keys():
						contents = contents.replace(i, str(self.replacements[i]))
					out.write(contents)
					out.close()

	def __copy_files(self, output_base, files={}):
		for f in files:
			shutil.copyfile('template/' + f, join(output_base, f))

	def __copytree(self, src, dst, symlinks=False, ignore=None):
	    for item in os.listdir(src):
	        s = os.path.join(src, item)
	        d = os.path.join(dst, item)
	        if os.path.isdir(s):
	            shutil.copytree(s, d, symlinks, ignore)
	        else:
	            shutil.copy2(s, d)

	def __app_base_folder(self):
		return join(self.output_folder, self.app_name)

	def create(self):
		self.__create_output_folder()
		self.__create_readme()
		self.__create_folder_structure()
		self.__copy_source_files()
		self.__run_replacement()




def create_app(app_name, package_name, app_class_prefix, 
	compile_sdk_version, min_sdk_version, target_sdk_version, output_folder=None):
	if output_folder is None:
		output_folder = app_name + 'Project'

	template = TemplateWriter(app_name, package_name, app_class_prefix, 
		compile_sdk_version, min_sdk_version, target_sdk_version, output_folder)
	template.create()


def main():
	parser = argparse.ArgumentParser(description='Create an Android App skeleton')
	parser.add_argument('app_name', metavar='APP_NAME', help='the name of the generated application')
	parser.add_argument('package_name', metavar='PACKAGE', help='the name of the generated package')
	parser.add_argument('app_class_prefix', metavar='PREFIX', help="prefix for import classes. Eg 'Awesome' for AwesomeApp or AwesomeContentProvider")
	parser.add_argument('compile_sdk_version', metavar='COMPILE_SDK_VERSION', type=int, help="Sdk version to use to compile the app. Eg 19")
	parser.add_argument('min_sdk_version', metavar='MIN_SDK_VERSION', type=int, help="Minimum sdk version the app targets. Eg 14")
	parser.add_argument('target_sdk_version', metavar='TARGET_SDK_VERSION', type=int, help="Target sdk version the app targets. Eg 19")
	parser.add_argument('-d', '--output_directory', metavar='OUTPUT_DIR', help="Output direct for the generated project")


	args = parser.parse_args()
	create_app(args.app_name, args.package_name, args.app_class_prefix, 
		args.compile_sdk_version, args.min_sdk_version, args.target_sdk_version, args.output_directory)
	

if __name__ == '__main__':
	main()


