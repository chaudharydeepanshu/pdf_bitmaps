import 'dart:typed_data';

import 'package:pdf_bitmaps/src/pdf_bitmaps_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

abstract class PdfBitmapsPlatform extends PlatformInterface {
  /// Constructs a PdfBitmapsPlatform.
  PdfBitmapsPlatform() : super(token: _token);

  static final Object _token = Object();

  static PdfBitmapsPlatform _instance = MethodChannelPdfBitmaps();

  /// The default instance of [PdfBitmapsPlatform] to use.
  ///
  /// Defaults to [MethodChannelPdfBitmaps].
  static PdfBitmapsPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PdfBitmapsPlatform] when
  /// they register themselves.
  static set instance(PdfBitmapsPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<int?> pdfPageCount({PDFPageCountParams? params}) async {
    throw UnimplementedError('pdfPageCount() has not been implemented.');
  }

  Future<Uint8List?> pdfBitmap({PDFBitmapParams? params}) async {
    throw UnimplementedError('pdfBitmap() has not been implemented.');
  }

  Future<List<Uint8List>?> pdfBitmaps({PDFBitmapsParams? params}) async {
    throw UnimplementedError('pdfBitmaps() has not been implemented.');
  }

  Future<PageSizeInfo?> pdfPageSize({PDFPageSizeParams? params}) async {
    throw UnimplementedError('pdfPageSize() has not been implemented.');
  }

  Future<String?> cancelBitmaps() async {
    throw UnimplementedError('cancelBitmaps() has not been implemented.');
  }
}
